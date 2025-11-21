declare module "react-jdenticon" {
  import type { HTMLAttributes } from "react";
  export type JdenticonProps = HTMLAttributes<SVGElement> & {
    size?: number | string;
    value: string;
  };
  export default function Jdenticon(props: JdenticonProps): JSX.Element;
}

