import { PopMsg } from "@/store/pops";

const writeToClipborad = async (text: string) => {
  await navigator.clipboard.writeText(text);
  PopMsg({
    title: "复制成功",
    description: "",
    type: "success",
  });
};

export function ClipedLabel({ text }: { text: string }) {
  return (
    <div
      className="hover:underline cursor-grab"
      onClick={() => {
        writeToClipborad(text);
      }}
    >
      {text}
    </div>
  );
}
