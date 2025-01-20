interface LoginButtonProps {
  children?: React.ReactNode;
  onClick: () => void;
}
export default function LoginButton(props: LoginButtonProps) {
  return (
    <button
      className="bg-white/10 backdrop-blur-lg border border-white/30 rounded-lg px-4 py-2 text-white text-base outline-none w-full box-border focus:border-white/50"
      onClick={props.onClick}
    >
      {props.children}
    </button>
  );
}
