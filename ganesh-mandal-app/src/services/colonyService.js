import { colonies as mockColonies } from '../mock-data';

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

let colonies = [...mockColonies];
let nextId = colonies.length + 1;

export const getColonies = async () => {
  await delay();
  return colonies.map((c) => ({ ...c }));
};

export const getColonyById = async (id) => {
  await delay();
  const colony = colonies.find((c) => c.id === id);
  if (!colony) throw new Error(`Colony with id ${id} not found`);
  return { ...colony };
};

export const addColony = async (colony) => {
  await delay();
  const newColony = { ...colony, id: nextId++ };
  colonies.push(newColony);
  return { ...newColony };
};

export const updateColony = async (id, colony) => {
  await delay();
  const index = colonies.findIndex((c) => c.id === id);
  if (index === -1) throw new Error(`Colony with id ${id} not found`);
  colonies[index] = { ...colonies[index], ...colony, id };
  return { ...colonies[index] };
};

export const deleteColony = async (id) => {
  await delay();
  const index = colonies.findIndex((c) => c.id === id);
  if (index === -1) throw new Error(`Colony with id ${id} not found`);
  const deleted = colonies.splice(index, 1);
  return { ...deleted[0] };
};

const colonyService = {
  getColonies,
  getColonyById,
  addColony,
  updateColony,
  deleteColony,
};

export default colonyService;
