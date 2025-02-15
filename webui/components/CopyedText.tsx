import { PopMsg } from "@/store/pops";
import { Code } from "@heroui/code";
import { FaRegCopy } from "react-icons/fa6";

interface CopyedTextProps {
  value: string;
}

export const CopyToClipBoard = (value: string) => {
  navigator.clipboard.writeText(value);
  PopMsg({
    title: "复制成功",
    description: "",
    type: "success",
  });
};
export default function CopyedText({ value }: CopyedTextProps) {
  return (
    <Code className="flex items-center justify-between p-4 text-base rounded-2xl">
      <div>{value}</div>
      <button onClick={() => CopyToClipBoard(value)}>
        <FaRegCopy />
      </button>
    </Code>
  );
}
