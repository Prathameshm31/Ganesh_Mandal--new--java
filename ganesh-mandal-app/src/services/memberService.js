import { members as mockMembers } from '../mock-data';

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

let members = [...mockMembers];
let nextSeq = members.length + 1;

const generateId = () => `GM-${String(nextSeq++).padStart(4, '0')}`;

const sortMembers = (list, sortBy = 'id', sortOrder = 'asc') => {
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

export const getMembers = async ({ page = 1, limit = 10, sortBy = 'id', sortOrder = 'asc' } = {}) => {
  await delay();
  const sorted = sortMembers(members, sortBy, sortOrder);
  return paginate(sorted, page, limit);
};

export const getMemberById = async (id) => {
  await delay();
  const member = members.find((m) => m.id === id);
  if (!member) throw new Error(`Member with id ${id} not found`);
  return { ...member };
};

export const addMember = async (member) => {
  await delay();
  const newMember = { ...member, id: generateId(), joinDate: new Date().toISOString().split('T')[0] };
  members.unshift(newMember);
  return { ...newMember };
};

export const updateMember = async (id, member) => {
  await delay();
  const index = members.findIndex((m) => m.id === id);
  if (index === -1) throw new Error(`Member with id ${id} not found`);
  members[index] = { ...members[index], ...member, id };
  return { ...members[index] };
};

export const deleteMember = async (id) => {
  await delay();
  const index = members.findIndex((m) => m.id === id);
  if (index === -1) throw new Error(`Member with id ${id} not found`);
  const deleted = members.splice(index, 1);
  return { ...deleted[0] };
};

export const searchMembers = async (query) => {
  await delay();
  const q = query.toLowerCase();
  return members.filter(
    (m) =>
      m.fullName?.toLowerCase().includes(q) ||
      m.mobileNumber?.includes(q) ||
      m.email?.toLowerCase().includes(q) ||
      m.colony?.toLowerCase().includes(q) ||
      m.houseNumber?.toLowerCase().includes(q) ||
      m.id?.toLowerCase().includes(q) ||
      m.occupation?.toLowerCase().includes(q)
  );
};

export const filterMembers = async (criteria) => {
  await delay();
  let filtered = [...members];
  if (criteria.status) {
    filtered = filtered.filter((m) => m.status === criteria.status);
  }
  if (criteria.colony) {
    filtered = filtered.filter((m) => m.colony === criteria.colony);
  }
  if (criteria.occupation) {
    filtered = filtered.filter((m) => m.occupation === criteria.occupation);
  }
  return filtered;
};

export const getAllMembers = async () => {
  await delay();
  return [...members];
};

const memberService = {
  getMembers,
  getMemberById,
  addMember,
  updateMember,
  deleteMember,
  searchMembers,
  filterMembers,
  getAllMembers,
};

export default memberService;
