'use server';

import { GET, POST, PUT } from '@/lib/fetch';
import { convertToLocalTime } from '@/lib/utils';
import { revalidatePath } from 'next/cache';
import { UnapprovedTweet } from '@/types';

const { API } = process.env;

export async function updatePersonality(prevState: any, formData: FormData) {
  const id = formData.get('id');
  const tone = formData.get('tone');
  const interest = formData.get('interest');

  if (tone === null || interest === null) {
    return { error: true, message: 'Please provide both tone and interest data to update the personality.' };
  }

  const response = await PUT(`/influencer/${id}/personality`, {
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
  const gender = formData.get('gender');
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
    gender === null ||
    hairColor === null ||
    hairLength === null ||
    height === null ||
    skinTone === null ||
    style === null
  ) {
    return { error: true, message: 'Please provide all appearance data to update the appearance.' };
  }

  const response = await PUT(`/influencer/${id}/appearance`, {
    bodyBuild: bodyBuild.toString(),
    eyeColor: eyeColor.toString(),
    eyeShape: eyeShape.toString(),
    faceFeatures: faceFeatures.toString().split(','),
    faceShape: faceShape.toString(),
    gender: gender.toString(),
    hairColor: hairColor.toString(),
    hairLength: hairLength.toString(),
    height: height.toString(),
    skinTone: skinTone.toString(),
    style: style.toString(),
  });

  if (response.status >= 300) {
    return { error: true, message: 'There was an issue updating the appearance. Please try again later.' };
  }

  return { error: false, message: 'Appearance updated successfully. Next update the base image.' };
}

export async function tweetText(prevState: any, formData: FormData) {
  const id = formData.get('id');
  const response = await POST(`/twitter/tweet/${id}`, {});

  if (response.status >= 300) {
    return { error: true, message: 'There was an issue creating the Twitter text post. Please try again later.' };
  }

  const { text, link } = response;

  return { error: false, message: `Twitter text post created successfully! Twitter post: ${text}`, link: link };
}

export async function tweetImage(prevState: any, formData: FormData) {
  const id = formData.get('id');
  const response = await POST(`/twitter/tweet/${id}/image`, {});

  if (response.status >= 300) {
    return { error: true, message: 'There was an issue generating the tweet and image. Please try again later.' };
  }

  const { text, link } = response;

  return { error: false, message: `Tweet with image created successfully! Post is waiting for approval.` };
}

export async function updateTwitterBaseImage(id: string) {
  await fetch(`${process.env.API}/stability/influencer/${id}/base-image`, { method: 'PUT' });
}

export type SchedulerData = {
  hour: string;
  minute: string;
  meridiem: string;
  workDays: string[];
};

export async function updateScheduler(id: string, data: SchedulerData) {
  const time = convertToLocalTime(data);
  const response = await PUT(`/influencer/${id}/scheduler`, {
    scheduledTime: time.toString(),
    scheduledDays: data.workDays,
  });

  return response.status >= 300
    ? { error: true, message: 'There was an issue updating the scheduler. Please try again later.' }
    : { error: false, message: `Scheduler updated successfully!` };
}

export async function getUnapprovedTweet(): Promise<UnapprovedTweet[]> {
  return await GET('/twitter/unapproved/tweets', 'no-cache');
}

export async function retryImageGeneration(id: string) {
  const response = await PUT(`/twitter/tweet/${id}/retry`, {});

  revalidatePath('/approval', 'page');

  return response;
}

export async function approveTweet(id: string) {
  const response = await PUT(`/twitter/tweet/${id}/approve`, {});

  revalidatePath('/approval', 'page');

  return response;
}
