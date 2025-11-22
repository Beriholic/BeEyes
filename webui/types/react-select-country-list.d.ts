declare module "react-select-country-list" {
  export interface CountryOption {
    value: string;
    label: string;
  }

  export default function CountrySelect(): {
    getData: () => CountryOption[];
    getValue: (label: string) => string | undefined;
    getLabel: (value: string) => string | undefined;
  };
}

