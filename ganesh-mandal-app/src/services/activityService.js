import { activities as mockActivities } from '../mock-data';

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

let activities = [...mockActivities];
let nextId = activities.length + 1;

export const getActivities = async () => {
  await delay();
  return activities.map((a) => ({ ...a }));
};

export const getActivityById = async (id) => {
  await delay();
  const activity = activities.find((a) => a.id === id);
  if (!activity) throw new Error(`Activity with id ${id} not found`);
  return { ...activity };
};

export const addActivity = async (activity) => {
  await delay();
  const newActivity = { ...activity, id: nextId++ };
  activities.push(newActivity);
  return { ...newActivity };
};

export const updateActivity = async (id, activity) => {
  await delay();
  const index = activities.findIndex((a) => a.id === id);
  if (index === -1) throw new Error(`Activity with id ${id} not found`);
  activities[index] = { ...activities[index], ...activity, id };
  return { ...activities[index] };
};

export const deleteActivity = async (id) => {
  await delay();
  const index = activities.findIndex((a) => a.id === id);
  if (index === -1) throw new Error(`Activity with id ${id} not found`);
  const deleted = activities.splice(index, 1);
  return { ...deleted[0] };
};

const activityService = {
  getActivities,
  getActivityById,
  addActivity,
  updateActivity,
  deleteActivity,
};

export default activityService;
