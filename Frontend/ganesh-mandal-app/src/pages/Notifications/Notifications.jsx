import { useState } from 'react';
import {
  Box,
  Typography,
  ToggleButtonGroup,
  ToggleButton,
  Card,
  CardContent,
  Chip,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  CircularProgress,
} from '@mui/material';
import {
  MdNotifications,
  MdNotificationsActive,
  MdCampaign,
  MdCheckCircle,
  MdRadioButtonUnchecked,
  MdCelebration,
  MdEvent,
  MdInfo,
} from 'react-icons/md';
import { toast } from 'react-toastify';

const allNotifications = [
  { id: 1, type: 'Reminders', title: 'Pending Donation Reminder', message: 'Reminder: Your donation for 2026 is pending', icon: MdNotificationsActive, unread: true, timestamp: '2 hours ago' },
  { id: 2, type: 'Reminders', title: 'Upcoming Activity', message: 'Ganesh Idol Installation is tomorrow at 10:00 AM', icon: MdEvent, unread: true, timestamp: '1 day ago' },
  { id: 3, type: 'Announcements', title: 'Festival Announcements', message: 'Ganpati Festival starts from September 7', icon: MdCelebration, unread: false, timestamp: '3 days ago' },
  { id: 4, type: 'Announcements', title: 'Meeting Notice', message: 'Committee meeting this Sunday at 11:00 AM', icon: MdCampaign, unread: true, timestamp: '5 days ago' },
  { id: 5, type: 'Reminders', title: 'Membership Renewal', message: 'Your annual membership is due for renewal', icon: MdNotificationsActive, unread: false, timestamp: '1 week ago' },
  { id: 6, type: 'Announcements', title: 'New Initiative', message: 'Tree plantation drive this weekend - join us!', icon: MdInfo, unread: false, timestamp: '2 weeks ago' },
];

const tabs = ['All', 'Reminders', 'Announcements'];

export default function Notifications() {
  const [tab, setTab] = useState('All');
  const [notifications, setNotifications] = useState(allNotifications);
  const [loading, setLoading] = useState(false);

  const filtered = tab === 'All' ? notifications : notifications.filter(n => n.type === tab);

  const toggleRead = (id) => {
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, unread: !n.unread } : n)
    );
    toast.success('Notification status updated');
  };

  const unreadCount = notifications.filter(n => n.unread).length;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 1 }}>
        <Box>
          <Typography variant="h5" fontWeight={700}>Notifications</Typography>
          {unreadCount > 0 && (
            <Typography variant="body2" color="text.secondary">{unreadCount} unread</Typography>
          )}
        </Box>
      </Box>

      <ToggleButtonGroup
        value={tab}
        exclusive
        onChange={(_, v) => v && setTab(v)}
        size="small"
        sx={{ mb: 3 }}
      >
        {tabs.map(t => (
          <ToggleButton key={t} value={t}>{t}</ToggleButton>
        ))}
      </ToggleButtonGroup>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>
      ) : filtered.length === 0 ? (
        <Typography color="text.secondary" textAlign="center" py={8}>No notifications found</Typography>
      ) : (
        <List>
          {filtered.map(notification => (
            <Card
              key={notification.id}
              sx={{
                mb: 1.5,
                bgcolor: notification.unread ? 'action.hover' : 'background.paper',
                borderLeft: 4,
                borderColor: notification.unread ? 'primary.main' : 'transparent',
              }}
            >
              <ListItem
                secondaryAction={
                  <IconButton edge="end" onClick={() => toggleRead(notification.id)}>
                    {notification.unread ? <MdCheckCircle color="#1976d2" /> : <MdRadioButtonUnchecked />}
                  </IconButton>
                }
              >
                <ListItemIcon sx={{ fontSize: 28, color: notification.unread ? 'primary.main' : 'text.secondary' }}>
                  <notification.icon />
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography variant="subtitle2" fontWeight={600}>{notification.title}</Typography>
                      {notification.unread && <Chip label="New" size="small" color="primary" />}
                      <Chip label={notification.type} size="small" variant="outlined" />
                    </Box>
                  }
                  secondary={
                    <>
                      <Typography variant="body2" color="text.primary">{notification.message}</Typography>
                      <Typography variant="caption" color="text.secondary">{notification.timestamp}</Typography>
                    </>
                  }
                />
              </ListItem>
            </Card>
          ))}
        </List>
      )}
    </Box>
  );
}
