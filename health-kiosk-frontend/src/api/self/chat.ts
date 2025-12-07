// src/api/chat.ts
import instance from "../axios"; // 按你要求的格式：从 ../axios 引入 axios 实例

export interface CorpusItem {
  id: number;
  request: string;
  response: string;
}

/**
 * 分页获取 corpus（每页 limit 条）
 * 与后端约定：GET /getCorpus?page=1&limit=4
 */
export function getCorpusApi(page = 1, limit = 4) {
  return instance.get("/llm/getCorpus", {
    params: { page, limit },
  });
}

/**
 * 更新语料：body { id, request?, response? }
 */
export function updateCorpusApi(payload: {
  id: number;
  request?: string;
  response?: string;
}) {
  return instance.post("/llm/updateCorpus", payload);
}

/**
 * 新增语料：body { request, response }
 */
export function addCorpusApi(payload: { request: string; response: string }) {
  return instance.post("/llm/addCorpus", payload);
}

/**
 * 一次性发送消息（非流式），后端接受 { model, req_id, input }
 * 返回后端的完整回复（非流）
 */
export function sendMessageApi(input: string, model?: string | null, req_id?: string | null) {
  return instance.post("/llm/chat", {
    model,
    req_id,
    input,
    stream: false,
  });
}

/**
 * 流式发送消息（放在同一文件中），注意：
 * - axios 在浏览器端无法稳定读取 response body 流，所以这里使用 fetch。
 * - 仍然与其它 API 同目录，满足“所有 api 写在一个 typescript 文件中”的要求。
 *
 * onChunk(chunk, done) 会被多次调用，done=true 表示流完成。
 */
export function sendMessageStreamApi(
  input: string,
  onChunk: (chunk: string, done: boolean) => void,
  options?: {
    model?: string | null;
    req_id?: string | null;
    signal?: AbortSignal;
    extraHeaders?: Record<string, string>;
  }
) {
  const url = "http://localhost:3000/llm/chat";
  const payload = {
    model: options?.model ?? null,
    req_id: options?.req_id ?? null,
    input,
    stream: true,
  };

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options?.extraHeaders || {}),
  };
  const token = localStorage.getItem("token");
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const signal = options?.signal;

  return fetch(url, {
    method: "POST",
    headers,
    body: JSON.stringify(payload),
    signal,
  })
    .then(async (resp) => {
      if (!resp.ok) {
        const txt = await resp.text().catch(() => "");
        throw new Error(`stream request failed: ${resp.status} ${txt}`);
      }

      if (!resp.body) {
        const text = await resp.text().catch(() => "");
        onChunk(text || "", true);
        return;
      }

      const reader = resp.body.getReader();
      const decoder = new TextDecoder();
      let buffer = ""; // 用于存储不完整的行

      try {
        while (true) {
          const { done, value } = await reader.read();
          
          if (done) {
            // 处理缓冲区中剩余的数据
            if (buffer.trim()) {
              processLine(buffer.trim());
            }
            onChunk("", true);
            break;
          }

          // 解码并添加到缓冲区
          buffer += decoder.decode(value, { stream: true });
          
          // 按行分割（SSE 格式是按行分隔的）
          const lines = buffer.split(/\r?\n/);
          
          // 保留最后一行（可能不完整）
          buffer = lines.pop() || "";
          
          // 处理完整的行
          for (const line of lines) {
            processLine(line);
          }
        }
      } catch (err) {
        throw err;
      } finally {
        try {
          await reader.cancel();
        } catch (e) {
          // ignore
        }
      }

      function processLine(line: string) {
        line = line.trim();
        if (!line) return;

        console.log(line)

        // 处理 SSE 格式: data: {...}
        if (line.startsWith("data:")) {
          const dataContent = line.substring(5).trim();
          
          // 跳过 SSE 的结束标记
          if (dataContent === "[DONE]" || dataContent === "[done]") {
            return;
          }

          try {
            const parsed = JSON.parse(dataContent);
            
            // 根据你提供的格式解析
            // choices[0].delta.content 是内容
            if (parsed.choices && 
                Array.isArray(parsed.choices) && 
                parsed.choices.length > 0) {
              
              const choice = parsed.choices[0];
              const content = choice.delta?.content;
              
              // 只有当 content 存在且不为 null 时才输出
              if (content) {
                onChunk(String(content), false);
              }
              
              // 检查是否完成（finish_reason 不为 null）
              if (choice.finish_reason !== null && choice.finish_reason !== undefined) {
                // 这个 chunk 标志着流的结束，但不在这里调用 onChunk done
                // 因为我们在 reader.read() done 时统一处理
              }
            }
          } catch (e) {
            console.error("Failed to parse SSE data:", e, "Line:", dataContent);
            // 解析失败，忽略这行
          }
        } else if (line.startsWith(":")) {
          // SSE 注释行，忽略
          return;
        } else {
          // 非标准格式，尝试直接解析
          try {
            const parsed = JSON.parse(line);
            if (parsed.choices?.[0]?.delta?.content) {
              onChunk(String(parsed.choices[0].delta.content), false);
            }
          } catch (e) {
            // 不是 JSON，忽略
          }
        }
      }
    })
    .catch((err) => {
      throw err;
    });
}