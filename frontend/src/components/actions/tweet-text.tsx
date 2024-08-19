import { SubmitButton } from '@/components/submit-button';
import { useFormState } from 'react-dom';
import { tweetText } from '@/actions';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import Link from 'next/link';
import { Button } from '@/components/ui/button';

export default function TweetText({ twitterId }: Readonly<{ twitterId: string }>) {
  const [state, formAction] = useFormState(tweetText, {
    error: false,
    message: '',
    link: '',
  });

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Tweet Text</CardTitle>
        </CardHeader>
        <CardContent className="grid">
          <CardDescription>Create AI-generated tweets to enhance your influencer's online presence and interaction.</CardDescription>
        </CardContent>
        <CardFooter className="grid gap-4">
          <form action={formAction}>
            <input type="hidden" name="id" value={twitterId} readOnly={true} />
            <SubmitButton state={state} isLoading={true}>
              Create Tweet
            </SubmitButton>
          </form>
          {state.link && (
            <div className="flex justify-end">
              <Link href={state.link} target="_blank">
                <Button type="button" variant="outline">
                  See Tweet on X
                </Button>
              </Link>
            </div>
          )}
        </CardFooter>
      </Card>
    </>
  );
}
