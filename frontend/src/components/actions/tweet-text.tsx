import { SubmitButton } from '@/components/submit-button';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { useFormState } from 'react-dom';
import { createTwitterTextPost } from '@/actions';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

export default function TextGeneration({ twitterId }: Readonly<{ twitterId: string }>) {
  const [state, formAction] = useFormState(createTwitterTextPost, {
    error: false,
    message: '',
    link: '',
  });

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Tweet Text</CardTitle>
          <CardDescription>Create AI-generated tweets to enhance your influencer's online presence and interaction.</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-2">
          <div>
            <form action={formAction}>
              <input type="hidden" name="id" value={twitterId} readOnly={true} />
              <SubmitButton state={state} isLoading={true}>
                Create Tweet
              </SubmitButton>
            </form>
          </div>
          {state.link && (
            <div className="flex justify-end">
              <Link href={state.link} target="_blank">
                <Button type="button" variant="outline">
                  See Tweet on X
                </Button>
              </Link>
            </div>
          )}
        </CardContent>
      </Card>
    </>
  );
}
