import { useFormState } from 'react-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Interest, Personality, Tone } from '@/types';
import { SubmitButton } from '@/components/submit-button';
import { updatePersonality } from '@/actions';
import ToggleList from '@/components/attributes/toggle-list';

type PersonalityCardProps = {
  id: string;
  data: Personality;
};

export default function PersonalityCard({ id, data }: PersonalityCardProps) {
  const [state, formAction] = useFormState(updatePersonality, {
    error: false,
    message: '',
  });

  return (
    <>
      <form action={formAction}>
        <input type="hidden" name="id" value={id} readOnly={true} />
        <Card className="w-[500px]">
          <CardHeader>
            <CardTitle>Personality</CardTitle>
            <CardDescription>
              Adjust your influencer personality to better match their online presence and engagement style. Select the tones and interests
              that best describe their unique character traits and preferences.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid w-full items-center gap-2">
              <ToggleList id="tone" label="Tone" enums={Tone} data={data.tone} />
              <ToggleList id="interest" label="Interest" enums={Interest} data={data.interest} />
            </div>
          </CardContent>
          <CardFooter>
            <SubmitButton state={state}>Update</SubmitButton>
          </CardFooter>
        </Card>
      </form>
    </>
  );
}
