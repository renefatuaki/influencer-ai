import { useFormState } from 'react-dom';
import { createTwitterTextPost } from '@/actions';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { SubmitButton } from '@/components/submit-button';

export default function TweetImage({ twitterId }: Readonly<{ twitterId: string }>) {
  const [state, formAction] = useFormState(createTwitterTextPost, {
    error: false,
    message: '',
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tweet Image</CardTitle>
      </CardHeader>
      <CardContent className="grid">
        <CardDescription>Create AI-generated tweets with images. Approval required before posting. Find the tweet under the approval section.</CardDescription>
      </CardContent>
      <CardFooter className="grid">
        <form action={formAction}>
          <input type="hidden" name="id" value={twitterId} readOnly={true} />
          <SubmitButton state={state} isLoading={true}>
            Generate Tweet with Image
          </SubmitButton>
        </form>
      </CardFooter>
    </Card>
  );
}
