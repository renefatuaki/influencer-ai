import { Label } from '@/components/ui/label';
import { Toggle } from '@/components/ui/toggle';
import { useState } from 'react';

type ToggleListProps<T extends object> = {
  id: string;
  label: string;
  enums: T;
  data: string[];
};

export default function ToggleList<T extends object>({ id, label, enums, data }: ToggleListProps<T>) {
  const options: string[] = Object.keys(enums);
  const [selectedOptions, setSelectedOptions] = useState<string[]>(data);

  const toggleOption = (option: string) => {
    setSelectedOptions((prevState) => {
      return prevState.includes(option) ? prevState.filter((selectedOption) => selectedOption !== option) : [...prevState, option];
    });
  };

  return (
    <>
      <Label className="text-base font-bold" htmlFor={id}>
        {label}
      </Label>
      <div id={id} className="flex flex-row flex-wrap justify-center gap-2">
        {options.map((option) => (
          <Toggle
            key={option}
            type="button"
            className="text-sm p-2"
            defaultPressed={selectedOptions.includes(option)}
            onClick={() => toggleOption(option)}
          >
            {option.toLowerCase()}
          </Toggle>
        ))}
      </div>
      <input type="hidden" name={id} value={selectedOptions} readOnly={true} />
    </>
  );
}
