'use server';

import { PATCH } from '@/lib/fetch';

export async function updatePersonality(prevState: any, formData: FormData) {
  const id = formData.get('id');
  const tone = formData.get('tone');
  const interest = formData.get('interest');

  if (tone === null || interest === null) {
    return { error: true, message: 'Please provide both tone and interest data to update the personality.' };
  }

  const response = await PATCH(`/influencer/${id}/personality`, {
    tone: tone.toString().split(','),
    interest: interest.toString().split(','),
  });

  if (response.status >= 300) {
    return { error: true, message: 'There was an issue updating the personality. Please try again later.' };
  }

  return { error: false, message: 'Personality updated successfully!' };
}

export async function updateAppearance(prevState: any, formData: FormData) {
  const id = formData.get('id');
  const bodyBuild = formData.get('body-build');
  const eyeColor = formData.get('eye-color');
  const eyeShape = formData.get('eye-shape');
  const faceFeatures = formData.get('face-features');
  const faceShape = formData.get('face-shape');
  const hairColor = formData.get('hair-color');
  const hairLength = formData.get('hair-length');
  const height = formData.get('height');
  const skinTone = formData.get('skin-tone');
  const style = formData.get('style');

  if (
    bodyBuild === null ||
    eyeColor === null ||
    eyeShape === null ||
    faceFeatures === null ||
    faceShape === null ||
    hairColor === null ||
    hairLength === null ||
    height === null ||
    skinTone === null ||
    style === null
  ) {
    return { error: true, message: 'Please provide all appearance data to update the appearance.' };
  }

  const response = await PATCH(`/influencer/${id}/appearance`, {
    bodyBuild: bodyBuild.toString(),
    eyeColor: eyeColor.toString(),
    eyeShape: eyeShape.toString(),
    faceFeatures: faceFeatures.toString().split(','),
    faceShape: faceShape.toString(),
    hairColor: hairColor.toString(),
    hairLength: hairLength.toString(),
    height: height.toString(),
    skinTone: skinTone.toString(),
    style: style.toString(),
  });

  if (response.status >= 300) {
    return { error: true, message: 'There was an issue updating the appearance. Please try again later.' };
  }

  return { error: false, message: 'Appearance updated successfully!' };
}
