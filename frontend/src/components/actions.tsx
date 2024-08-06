import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { createTwitterTextPost } from '@/actions';
import { useFormState } from 'react-dom';
import { SubmitButton } from '@/components/submit-button';
import Link from 'next/link';

export default function Actions({ influencerId }: { influencerId: string }) {
  const [state, formAction] = useFormState(createTwitterTextPost, {
    error: false,
    message: '',
    link: '',
  });

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>Actions</CardTitle>
          <CardDescription>
            Utilize AI to generate engaging text or images for your influencer's Twitter posts, enhancing their online presence and
            interaction.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form action={formAction}>
            <input type="hidden" name="id" value={influencerId} readOnly={true} />
            <SubmitButton state={state} isLoading={true}>
              Post Text on Twitter
            </SubmitButton>
          </form>
        </CardContent>
        {state.link && (
          <CardFooter className="flex justify-end">
            <Link href={state.link} target="_blank">
              <Button type="button" variant="outline">
                See Post on Twitter
              </Button>
            </Link>
          </CardFooter>
        )}
      </Card>
    </>
  );
}
