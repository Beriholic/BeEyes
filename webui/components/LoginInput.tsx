interface LoginInputProps {
  type?: string | undefined;
  placeholder: string;
  value: string;
  onChange: (value: string) => void;
}

export default function LoginInput(props: LoginInputProps) {
  return (
    <input
      className="bg-white/10 backdrop-blur-lg border border-white/30 rounded-lg px-4 py-2 text-white text-base outline-none w-full box-border focus:border-white/50"
      placeholder={props.placeholder}
      type={props.type ?? "text"}
      value={props.value}
      onChange={(e) => props.onChange(e.target.value)}
    />
  );
}
