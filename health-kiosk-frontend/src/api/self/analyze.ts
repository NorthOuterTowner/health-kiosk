// api/self/analyze.ts
import instance from "../axios";

export function analyzeHealthApi(start: string, end: string, user_id: string | null, model?: string | null) {
    return instance.post("/llm/analyze", {
        start,
        end,
        user_id
    });
}
