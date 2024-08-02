'use client';

import { useFormStatus } from 'react-dom';
import { Button } from '@/components/ui/button';
import { ReactNode } from 'react';
import AlertResponse from '@/components/alert-response';

type SubmitButtonProps = {
  children: ReactNode;
  state: { error: boolean; message: string };
};

export function SubmitButton({ children, state }: SubmitButtonProps) {
  const { pending } = useFormStatus();

  return (
    <>
      <div className="grid w-full items-center gap-2">
        <Button type="submit" disabled={pending}>
          {children}
        </Button>
        {state && <AlertResponse error={state.error} message={state.message} />}
      </div>
    </>
  );
}
