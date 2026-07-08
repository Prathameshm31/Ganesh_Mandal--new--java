import { members, donations, colonies, activities } from '../mock-data';

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

export const getDashboardStats = async () => {
  await delay();
  const totalMembers = members.length;
  const activeMembers = members.filter((m) => m.status === 'Active').length;
  const totalDonations = donations.reduce((sum, d) => sum + d.amount, 0);
  const totalDonationCount = donations.length;
  const avgDonation = totalDonationCount > 0 ? Math.round(totalDonations / totalDonationCount) : 0;
  const totalColonies = colonies.length;
  const totalActivities = activities.length;
  const upcomingActivities = activities.filter((a) => a.status === 'Upcoming').length;

  const today = new Date().toISOString().split('T')[0];
  const todayDonations = donations.filter((d) => d.donationDate === today);
  const todayCollection = todayDonations.reduce((sum, d) => sum + d.amount, 0);

  const onlineModes = ['UPI', 'Google Pay', 'PhonePe', 'Paytm', 'Bank Transfer'];
  const onlineDonations = donations.filter((d) => onlineModes.includes(d.paymentMode));
  const onlineCollection = onlineDonations.reduce((sum, d) => sum + d.amount, 0);

  const cashDonations = donations.filter((d) => d.paymentMode === 'Cash');
  const cashCollection = cashDonations.reduce((sum, d) => sum + d.amount, 0);

  const currentYear = new Date().getFullYear();
  const thisYearDonations = donations.filter((d) => d.donationDate.startsWith(String(currentYear)));
  const thisYearCollection = thisYearDonations.reduce((sum, d) => sum + d.amount, 0);

  const pendingMembers = members.filter((m) => {
    const memberDonations = donations.filter((d) => d.memberId === m.id && d.donationDate.startsWith(String(currentYear)));
    return memberDonations.length === 0;
  });

  return {
    totalMembers,
    activeMembers,
    totalDonations,
    totalDonationCount,
    avgDonation,
    totalColonies,
    totalActivities,
    upcomingActivities,
    todayCollection,
    onlineCollection,
    cashCollection,
    thisYearCollection,
    pendingMembers: pendingMembers.length,
  };
};

export const getMonthlyCollection = async () => {
  await delay();
  const monthlyMap = {};
  donations.forEach((d) => {
    const month = d.donationDate.substring(0, 7);
    if (!monthlyMap[month]) monthlyMap[month] = 0;
    monthlyMap[month] += d.amount;
  });
  return Object.entries(monthlyMap)
    .map(([month, amount]) => ({ month, amount }))
    .sort((a, b) => a.month.localeCompare(b.month))
    .slice(-12);
};

export const getColonyWiseCollection = async () => {
  await delay();
  const colonyMap = {};
  donations.forEach((d) => {
    if (!colonyMap[d.colony]) colonyMap[d.colony] = 0;
    colonyMap[d.colony] += d.amount;
  });
  return Object.entries(colonyMap).map(([colonyName, amount]) => ({
    colonyName,
    amount,
  }));
};

export const getPaymentModeBreakdown = async () => {
  await delay();
  const modeMap = {};
  donations.forEach((d) => {
    if (!modeMap[d.paymentMode]) modeMap[d.paymentMode] = 0;
    modeMap[d.paymentMode] += d.amount;
  });
  const total = Object.values(modeMap).reduce((s, v) => s + v, 0);
  return Object.entries(modeMap).map(([mode, amount]) => ({
    mode,
    amount,
    percentage: Math.round((amount / total) * 100),
  }));
};

export const getYearlyTrend = async () => {
  await delay();
  const yearMap = {};
  donations.forEach((d) => {
    const year = d.donationDate.substring(0, 4);
    if (!yearMap[year]) yearMap[year] = 0;
    yearMap[year] += d.amount;
  });
  return Object.entries(yearMap)
    .map(([year, amount]) => ({ year: Number(year), amount }))
    .sort((a, b) => a.year - b.year);
};

export const getTopDonors = async (limit = 5) => {
  await delay();
  const donorMap = {};
  donations.forEach((d) => {
    if (!donorMap[d.memberId]) donorMap[d.memberId] = 0;
    donorMap[d.memberId] += d.amount;
  });
  return Object.entries(donorMap)
    .map(([memberId, totalAmount]) => {
      const member = members.find((m) => m.id === memberId);
      return {
        memberId,
        memberName: member ? member.fullName : 'Unknown',
        totalAmount,
      };
    })
    .sort((a, b) => b.totalAmount - a.totalAmount)
    .slice(0, limit);
};

export const getRecentActivity = async () => {
  await delay();
  const recentDonations = [...donations]
    .sort((a, b) => new Date(b.donationDate) - new Date(a.donationDate))
    .slice(0, 5)
    .map((d) => ({ type: 'donation', ...d }));

  const recentMembers = [...members]
    .sort((a, b) => new Date(b.joinDate) - new Date(a.joinDate))
    .slice(0, 5)
    .map((m) => ({ type: 'member', ...m }));

  const upcoming = activities
    .filter((a) => a.status === 'Upcoming')
    .slice(0, 5)
    .map((a) => ({ type: 'activity', ...a }));

  return { recentDonations, recentMembers, upcomingActivities: upcoming };
};

const dashboardService = {
  getDashboardStats,
  getMonthlyCollection,
  getColonyWiseCollection,
  getPaymentModeBreakdown,
  getYearlyTrend,
  getTopDonors,
  getRecentActivity,
};

export default dashboardService;
