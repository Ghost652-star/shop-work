from langchain_core.callbacks import BaseCallbackHandler


class AgentDebugCallback(BaseCallbackHandler):

    def on_tool_start(self, serialized, input_str, *, run_id, **kwargs):
        tool_name = serialized.get("name", "unknown") if serialized else "unknown"
        print(f"\n[Callback] 调用工具: {tool_name}")
        print(f"[Callback]    输入: {input_str}")

    def on_tool_end(self, output, *, run_id, **kwargs):
        text = str(output.content) if hasattr(output, "content") else str(output)
        preview = text[:200] + "..." if len(text) > 200 else text
        print(f"[Callback] 工具返回: {preview}")

    def on_tool_error(self, error, *, run_id, **kwargs):
        print(f"[Callback] 工具出错: {error}")

    def on_llm_start(self, serialized, prompts, *, run_id, **kwargs):
        print(f"[Callback] LLM 开始思考...")

    def on_llm_end(self, response, *, run_id, **kwargs):
        print(f"[Callback] LLM 思考完毕")

    def on_llm_error(self, error, *, run_id, **kwargs):
        print(f"[Callback]  LLM 出错: {error}")

    def on_agent_action(self, action, *, run_id, **kwargs):
        print(f"[Callback] Agent 决策: 调用 {action.tool}，参数 {action.tool_input}")

    def on_agent_finish(self, output, *, run_id, **kwargs):
        result = output.get("return_values", {}).get("output", str(output))
        print(f"[Callback]  Agent 完成回复: {result[:200]}")
