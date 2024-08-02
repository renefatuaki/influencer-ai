import { useFormState } from 'react-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Appearance,
  BodyBuild,
  EyeColor,
  EyeShape,
  FaceFeatures,
  FaceShape,
  HairColor,
  HairLength,
  Height,
  SkinTone,
  Style,
} from '@/types';
import { SubmitButton } from '@/components/submit-button';
import { updateAppearance } from '@/actions';
import SelectInput from '@/components/attributes/select-input';
import ToggleList from '@/components/attributes/toggle-list';

type AppearanceCardProps = {
  id: string;
  data: Appearance;
};

export default function AppearanceCard({ id, data }: AppearanceCardProps) {
  const [state, formAction] = useFormState(updateAppearance, {
    error: false,
    message: '',
  });

  return (
    <>
      <form action={formAction}>
        <input type="hidden" name="id" value={id} readOnly={true} />
        <Card className="w-[1000px]">
          <CardHeader>
            <CardTitle>Appearance</CardTitle>
            <CardDescription>
              Transform your influencer&apos;s look to perfectly align with their online persona and engagement style. Choose the features
              and styles that highlight their distinctive physical attributes and preferences.
            </CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <div className="grid w-full items-center gap-2">
              <ToggleList id="face-features" label="Face Features" enums={FaceFeatures} data={data.faceFeatures} />
            </div>
            <div className="flex flex-row justify-between">
              <div className="flex flex-col gap-4 w-[300px]">
                <SelectInput id="body-build" label="Body Build" enums={BodyBuild} data={data.bodyBuild} />
                <SelectInput id="eye-color" label="Eye Color" enums={EyeColor} data={data.eyeColor} />
                <SelectInput id="eye-shape" label="Eye Shape" enums={EyeShape} data={data.eyeShape} />
              </div>
              <div className="flex flex-col gap-4 w-[300px]">
                <SelectInput id="face-shape" label="Face Shape" enums={FaceShape} data={data.faceShape} />
                <SelectInput id="hair-color" label="Hair Color" enums={HairColor} data={data.hairColor} />
                <SelectInput id="hair-length" label="Hair Length" enums={HairLength} data={data.hairLength} />
              </div>
              <div className="flex flex-col gap-4 w-[300px]">
                <SelectInput id="height" label="Height" enums={Height} data={data.height} />
                <SelectInput id="skin-tone" label="Skin Tone" enums={SkinTone} data={data.skinTone} />
                <SelectInput id="style" label="Style" enums={Style} data={data.style} />
              </div>
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
