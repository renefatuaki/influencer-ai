import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import TextGeneration from '@/components/actions/text-generation';

export default function Actions({ twitterId }: Readonly<{ twitterId: string }>) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Actions</CardTitle>
        <CardDescription>
          Utilize AI to generate engaging text or images for your influencer's Twitter posts, enhancing their online presence and interaction.
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-2">
        <TextGeneration twitterId={twitterId} />
      </CardContent>
    </Card>
  );
}
