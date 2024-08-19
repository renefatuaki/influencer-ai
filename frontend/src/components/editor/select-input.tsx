import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useState } from 'react';

type SelectInputProps<T extends object> = {
  className?: string;
  id: string;
  label: string;
  enums: T;
  data: string | undefined;
};

export default function SelectInput<T extends object>({ className, id, label, enums, data }: Readonly<SelectInputProps<T>>) {
  const options: string[] = Object.keys(enums);
  const [selectedOption, setSelectedOption] = useState<string>(data ?? '');

  return (
    <div className={`flex flex-col gap-2 my-2 ${className}`}>
      <Label className="text-base font-bold" htmlFor={label}>
        {label}
      </Label>
      <Select
        value={selectedOption}
        onValueChange={(value) => {
          setSelectedOption(value);
        }}
      >
        <SelectTrigger id={label}>
          <SelectValue />
        </SelectTrigger>
        <SelectContent position="popper">
          <SelectGroup>
            {options.map((option) => (
              <SelectItem key={option} value={option}>
                {option.toLowerCase().replace('_', ' ')}
              </SelectItem>
            ))}
          </SelectGroup>
        </SelectContent>
      </Select>
      <input type="hidden" name={id} value={selectedOption} readOnly={true} />
    </div>
  );
}
