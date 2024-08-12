'use client';

import { useFormStatus } from 'react-dom';
import { Button } from '@/components/ui/button';
import { ReactNode } from 'react';
import AlertResponse from '@/components/alert-response';
import Loading from '@/components/loading';

type SubmitButtonProps = {
  children: ReactNode;
  state: { error: boolean; message: string };
  isLoading?: boolean;
};

export function SubmitButton({ children, state, isLoading }: SubmitButtonProps) {
  const { pending } = useFormStatus();

  return (
    <>
      <div className="grid w-full gap-4">
        <Button type="submit" disabled={pending}>
          {children}
        </Button>
        {isLoading && pending && <Loading />}
        {!pending && state && <AlertResponse error={state.error} message={state.message} />}
      </div>
    </>
  );
}
