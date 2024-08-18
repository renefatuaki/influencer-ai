import { SubmitButton } from '@/components/submit-button';
import { useFormState } from 'react-dom';

type ActionResponseProps = {
  twitterId: string;
  action: (prevState: any, formData: FormData) => { error: boolean; message: string } | Promise<{ error: boolean; message: string }>;
};

export default function ActionResponse({ twitterId, action }: ActionResponseProps) {
  const [state, formAction] = useFormState(action, {
    error: false,
    message: '',
  });

  return (
    <>
      <form action={formAction} className="grid w-full">
        <input type="hidden" name="id" value={twitterId} readOnly={true} />
        <SubmitButton state={state} isLoading={true}>
          Start Image Generation
        </SubmitButton>
      </form>
    </>
  );
}
