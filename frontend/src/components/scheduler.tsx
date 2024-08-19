'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import { Button } from '@/components/ui/button';
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { updateScheduler } from '@/actions';
import { useToast } from '@/components/ui/use-toast';
import { WeekDays } from '@/types';
import { Checkbox } from '@/components/ui/checkbox';

const workDays = Object.keys(WeekDays).map((day) => ({ id: day, label: day.slice(0, 2) }));

const FormSchema = z.object({
  hour: z
    .string({
      required_error: 'Please select an hour.',
    })
    .regex(/^(0?[1-9]|1[0-2])$/, 'Invalid hour format. Please select a valid hour between 1 and 12.'),
  minute: z
    .string({
      required_error: 'Please select a minute.',
    })
    .regex(/^(0|15|30|45)$/, 'Invalid minute format. Please select a valid minute (00, 15, 30, 45).'),
  meridiem: z
    .string({
      required_error: 'Please select AM or PM.',
    })
    .regex(/^(AM|PM)$/, 'Invalid meridiem format. Please select either AM or PM.'),
  workDays: z.array(z.string()).refine((value) => value.some((item) => item), {
    message: 'You have to select at least one item.',
  }),
});

type SchedulerProps = {
  id: string;
  time?: string;
  days?: string[];
};

function convertHourTo12HourFormat(hour: string) {
  const hourInt = parseInt(hour, 10);
  return hourInt > 12 ? (hourInt - 12).toString().padStart(2, '0') : hour;
}

export function Scheduler({ id, time, days }: Readonly<SchedulerProps>) {
  const { toast } = useToast();

  const hour = time ? convertHourTo12HourFormat(time.split(':')[0]) : undefined;
  const minute = time ? time.split(':')[1] : undefined;
  const meridiem = time && parseInt(hour!, 10) > 12 ? 'PM' : 'AM';

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      hour: hour,
      minute: minute,
      meridiem: meridiem,
      workDays: days || [],
    },
  });

  function onSubmit(data: z.infer<typeof FormSchema>) {
    updateScheduler(id, data).then((response) => {
      if (response.error) {
        toast({
          variant: 'destructive',
          title: 'Error',
          description: <p>{response.message}</p>,
        });
      } else {
        toast({
          title: 'Success',
          description: <p>{response.message}</p>,
        });
      }
    });
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Scheduler</CardTitle>
        <CardDescription>
          Schedule the exact times at which image posts should be published on your influencer's Twitter account. This allows for precise control over the
          timing of content delivery, ensuring maximum engagement and visibility.
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-2">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <div className="grid md:grid-cols-3 gap-4">
              <FormField
                control={form.control}
                name="hour"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Hour</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={hour}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="hh" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="00">0</SelectItem>
                        <SelectItem value="01">1</SelectItem>
                        <SelectItem value="02">2</SelectItem>
                        <SelectItem value="03">3</SelectItem>
                        <SelectItem value="04">4</SelectItem>
                        <SelectItem value="05">5</SelectItem>
                        <SelectItem value="06">6</SelectItem>
                        <SelectItem value="07">7</SelectItem>
                        <SelectItem value="08">8</SelectItem>
                        <SelectItem value="09">9</SelectItem>
                        <SelectItem value="10">10</SelectItem>
                        <SelectItem value="11">11</SelectItem>
                        <SelectItem value="12">12</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="minute"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Minute</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={minute}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="mm" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="00">00</SelectItem>
                        <SelectItem value="15">15</SelectItem>
                        <SelectItem value="30">30</SelectItem>
                        <SelectItem value="45">45</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="meridiem"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Meridiem</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={meridiem}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="am/pm" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="AM">AM</SelectItem>
                        <SelectItem value="PM">PM</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <FormField
              control={form.control}
              name="workDays"
              render={() => (
                <FormItem>
                  <div className="mb-4">
                    <FormLabel className="text-base">Work Days</FormLabel>
                    <FormDescription>Select the days you want to schedule twitter posts.</FormDescription>
                  </div>
                  <div className="grid grid-cols-7 justify-items-center gap-4">
                    {workDays.map((day) => (
                      <FormField
                        key={day.id}
                        control={form.control}
                        name="workDays"
                        render={({ field }) => {
                          return (
                            <FormItem key={day.id} className="flex flex-col">
                              <FormLabel className="font-semibold">{day.label}</FormLabel>
                              <FormControl>
                                <Checkbox
                                  checked={field.value?.includes(day.id)}
                                  className="border-2"
                                  onCheckedChange={(checked) => {
                                    return checked
                                      ? field.onChange([...field.value, day.id])
                                      : field.onChange(field.value?.filter((value) => value !== day.id));
                                  }}
                                />
                              </FormControl>
                            </FormItem>
                          );
                        }}
                      />
                    ))}
                  </div>

                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" className="md:col-span-3">
              Submit
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
