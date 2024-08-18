import { SubmitButton } from '@/components/submit-button';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { useFormState } from 'react-dom';
import { createTwitterTextPost } from '@/actions';

export default function TextGeneration({ twitterId }: Readonly<{ twitterId: string }>) {
  const [state, formAction] = useFormState(createTwitterTextPost, {
    error: false,
    message: '',
    link: '',
  });

  return (
    <>
      <div>
        <form action={formAction}>
          <input type="hidden" name="id" value={twitterId} readOnly={true} />
          <SubmitButton state={state} isLoading={true}>
            Post Text on Twitter
          </SubmitButton>
        </form>
      </div>
      {state.link && (
        <div className="flex justify-end">
          <Link href={state.link} target="_blank">
            <Button type="button" variant="outline">
              See Post on Twitter
            </Button>
          </Link>
        </div>
      )}
    </>
  );
}
