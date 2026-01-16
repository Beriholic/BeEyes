package cv.beriholic.beeyes.service.dingtalk;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * DingTalk Robot API message DTOs.
 */
public sealed interface DingTalkMessage permits DingTalkMessage.Text,
                                              DingTalkMessage.Markdown {

    /**
     * Text message type.
     */
    record Text(String content) implements DingTalkMessage {
        @Override
        public String toJson() {
            JSONObject root = new JSONObject();
            root.set("msgtype", "text");
            JSONObject textObj = new JSONObject();
            textObj.set("content", content);
            root.set("text", textObj);
            return root.toString();
        }
    }

    /**
     * Markdown message type.
     */
    record Markdown(String title, String text) implements DingTalkMessage {
        @Override
        public String toJson() {
            JSONObject root = new JSONObject();
            root.set("msgtype", "markdown");
            JSONObject markdownObj = new JSONObject();
            markdownObj.set("title", title);
            markdownObj.set("text", text);
            root.set("markdown", markdownObj);
            return root.toString();
        }
    }

    /**
     * Serializes this message to JSON string.
     */
    String toJson();
}
