import { donations as mockDonations } from '../mock-data';

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

let donations = [...mockDonations];
let nextSeq = donations.length + 1;

const generateId = () => `DON-${String(nextSeq++).padStart(4, '0')}`;
const generateReceipt = () => `REC-${String(nextSeq).padStart(4, '0')}`;

const sortDonations = (list, sortBy = 'donationDate', sortOrder = 'desc') => {
  const sorted = [...list];
  sorted.sort((a, b) => {
    const aVal = a[sortBy] ?? '';
    const bVal = b[sortBy] ?? '';
    if (aVal < bVal) return sortOrder === 'asc' ? -1 : 1;
    if (aVal > bVal) return sortOrder === 'asc' ? 1 : -1;
    return 0;
  });
  return sorted;
};

const paginate = (list, page = 1, limit = 10) => {
  const start = (page - 1) * limit;
  return {
    data: list.slice(start, start + limit),
    total: list.length,
    page,
    limit,
    totalPages: Math.ceil(list.length / limit) || 1,
  };
};

export const getDonations = async ({ page = 1, limit = 10, sortBy = 'donationDate', sortOrder = 'desc' } = {}) => {
  await delay();
  const sorted = sortDonations(donations, sortBy, sortOrder);
  return paginate(sorted, page, limit);
};

export const getDonationById = async (id) => {
  await delay();
  const donation = donations.find((d) => d.id === id);
  if (!donation) throw new Error(`Donation with id ${id} not found`);
  return { ...donation };
};

export const addDonation = async (donation) => {
  await delay();
  const newDonation = {
    ...donation,
    id: generateId(),
    receiptNumber: generateReceipt(),
  };
  donations.unshift(newDonation);
  return { ...newDonation };
};

export const updateDonation = async (id, donation) => {
  await delay();
  const index = donations.findIndex((d) => d.id === id);
  if (index === -1) throw new Error(`Donation with id ${id} not found`);
  donations[index] = { ...donations[index], ...donation, id };
  return { ...donations[index] };
};

export const deleteDonation = async (id) => {
  await delay();
  const index = donations.findIndex((d) => d.id === id);
  if (index === -1) throw new Error(`Donation with id ${id} not found`);
  const deleted = donations.splice(index, 1);
  return { ...deleted[0] };
};

export const getDonationsByMember = async (memberId) => {
  await delay();
  return donations
    .filter((d) => d.memberId === memberId)
    .map((d) => ({ ...d }));
};

export const getDonationsByDateRange = async (startDate, endDate) => {
  await delay();
  const start = new Date(startDate);
  const end = new Date(endDate);
  return donations
    .filter((d) => {
      const date = new Date(d.donationDate);
      return date >= start && date <= end;
    })
    .map((d) => ({ ...d }));
};

export const getDonationsByColony = async (colony) => {
  await delay();
  return donations.filter((d) => d.colony === colony).map((d) => ({ ...d }));
};

export const getDonationsByPaymentMode = async (mode) => {
  await delay();
  return donations.filter((d) => d.paymentMode === mode).map((d) => ({ ...d }));
};

export const getAllDonations = async () => {
  await delay();
  return [...donations];
};

const donationService = {
  getDonations,
  getDonationById,
  addDonation,
  updateDonation,
  deleteDonation,
  getDonationsByMember,
  getDonationsByDateRange,
  getDonationsByColony,
  getDonationsByPaymentMode,
  getAllDonations,
};

export default donationService;
