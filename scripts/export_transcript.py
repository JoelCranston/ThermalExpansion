#!/usr/bin/env python3
"""Export a Claude Code session log (.jsonl) to a readable Markdown transcript.

Usage: export_transcript.py SESSION.jsonl OUT.md "Title" [--since ISO-TIMESTAMP]

Conversation in full; tool calls listed by name with their description; tool results collapsed
and truncated (1200 chars); internal reasoning omitted. The output goes under docs/context/,
which is gitignored - it exists only on Joel's machine, never pushed.
"""
import json
import sys
from datetime import datetime

TRUNC = 1200


def text_of(content):
    if isinstance(content, str):
        return content
    out = []
    for p in content or []:
        if p.get("type") == "text":
            out.append(p["text"])
    return "\n".join(out)


def main():
    args = sys.argv[1:]
    since = None
    if "--since" in args:
        i = args.index("--since")
        since = args[i + 1]
        del args[i:i + 2]
    src, dst, title = args[0], args[1], args[2]

    records = []
    for line in open(src):
        try:
            d = json.loads(line)
        except json.JSONDecodeError:
            continue
        if d.get("type") not in ("user", "assistant"):
            continue
        if since and (d.get("timestamp") or "") < since:
            continue
        records.append(d)

    # Tool results live in later "user" records keyed by tool_use_id.
    results = {}
    for d in records:
        if d["type"] != "user":
            continue
        for p in d["message"].get("content") or []:
            if isinstance(p, dict) and p.get("type") == "tool_result":
                results[p["tool_use_id"]] = p

    lines = [f"# Session transcript — {title}", ""]
    lines.append(f"Exported {datetime.now().strftime('%Y-%m-%d %H:%M')}. Source:\n`{src}`")
    lines.append("")
    lines.append("Conversation in full; tool calls listed by name with their description, and tool\n"
                 f"results collapsed and truncated to {TRUNC} characters each. Internal reasoning is not\n"
                 "included. Local only (gitignored via `docs/context/`), never pushed.")
    lines.append("")
    turn = 0
    for d in records:
        msg = d["message"]
        if d["type"] == "user":
            content = msg.get("content")
            if isinstance(content, list) and any(isinstance(p, dict) and p.get("type") == "tool_result" for p in content):
                continue
            text = text_of(content).strip()
            if not text or text.startswith("<") or text.startswith("[Image:"):
                continue
            turn += 1
            ts = (d.get("timestamp") or "")[:19].replace("T", " ")
            lines += ["", "---", "", f"## Turn {turn} — Joel  <sub>{ts}</sub>", "", text, ""]
        else:
            for p in msg.get("content") or []:
                if not isinstance(p, dict):
                    continue
                if p.get("type") == "text" and p["text"].strip():
                    lines += ["", "### Claude", "", p["text"].strip(), ""]
                elif p.get("type") == "tool_use":
                    inp = p.get("input", {})
                    desc = inp.get("description") or inp.get("file_path") or inp.get("query") or ""
                    lines.append(f"- 🔧 `{p['name']}` — {desc}")
                    r = results.get(p["id"])
                    if r is not None:
                        body = text_of(r.get("content"))
                        if len(body) > TRUNC:
                            body = body[:TRUNC] + f"\n… [truncated, {len(body)} chars]"
                        lines += ["", "<details><summary>tool result</summary>", "", "```", body.rstrip(), "```", "", "</details>", ""]
    lines[lines.index("", 8):lines.index("", 8)] = ["", f"**{turn} user turns.**"]
    open(dst, "w").write("\n".join(lines) + "\n")
    print(f"wrote {dst}: {turn} turns, {len(lines)} lines")


if __name__ == "__main__":
    main()
