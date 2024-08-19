import { useFormState } from 'react-dom';
import { createTwitterTextPost } from '@/actions';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { SubmitButton } from '@/components/submit-button';

export default function ImageGeneration({ twitterId }: Readonly<{ twitterId: string }>) {
  const [state, formAction] = useFormState(createTwitterTextPost, {
    error: false,
    message: '',
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>Actions</CardTitle>
        <CardDescription>
          Utilize AI to generate engaging text or images for your influencer's Twitter posts, enhancing their online presence and interaction.
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-2">
        <form action={formAction}>
          <input type="hidden" name="id" value={twitterId} readOnly={true} />
          <SubmitButton state={state} isLoading={true}>
            Generate Tweet with Image
          </SubmitButton>
        </form>
      </CardContent>
    </Card>
  );
}
