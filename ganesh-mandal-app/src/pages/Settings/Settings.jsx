import { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  TextField,
  Button,
  Divider,
  Grid,
} from '@mui/material';
import { MdSave } from 'react-icons/md';
import { toast } from 'react-toastify';

export default function Settings() {
  const [mandal, setMandal] = useState({
    name: 'Shree Ganesh Mandal',
    registrationNumber: 'MAH/2020/GANESH/001',
    establishedYear: '1985',
    about: 'A community-driven Ganesh Mandal serving devotees since 1985. We organize grand Ganesh festival celebrations and various social initiatives throughout the year.',
  });

  const [contact, setContact] = useState({
    address: '123, Ganpati Chowk, Near Dagdusheth Mandir, Pune - 411001',
    phone: '+91 98765 43210',
    email: 'info@shreeganeshmandal.org',
    website: 'www.shreeganeshmandal.org',
  });

  const [bank, setBank] = useState({
    bankName: 'State Bank of India',
    accountNumber: '**** **** **** 1234',
    ifscCode: 'SBIN0001234',
    branch: 'Pune Main Branch',
  });

  const [upi, setUpi] = useState({ upiId: 'ganeshmandal@upi' });

  const handleSave = () => {
    toast.success('Settings saved successfully');
  };

  return (
    <Box>
      <Typography variant="h5" fontWeight={700} mb={3}>Settings</Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} mb={2}>Mandal Information</Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField label="Mandal Name" value={mandal.name} onChange={e => setMandal({ ...mandal, name: e.target.value })} fullWidth />
                <TextField label="Registration Number" value={mandal.registrationNumber} onChange={e => setMandal({ ...mandal, registrationNumber: e.target.value })} fullWidth />
                <TextField label="Established Year" value={mandal.establishedYear} onChange={e => setMandal({ ...mandal, establishedYear: e.target.value })} fullWidth />
                <TextField label="About" value={mandal.about} onChange={e => setMandal({ ...mandal, about: e.target.value })} fullWidth multiline rows={3} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} mb={2}>Contact Details</Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField label="Address" value={contact.address} onChange={e => setContact({ ...contact, address: e.target.value })} fullWidth multiline rows={2} />
                <TextField label="Phone" value={contact.phone} onChange={e => setContact({ ...contact, phone: e.target.value })} fullWidth />
                <TextField label="Email" value={contact.email} onChange={e => setContact({ ...contact, email: e.target.value })} fullWidth />
                <TextField label="Website" value={contact.website} onChange={e => setContact({ ...contact, website: e.target.value })} fullWidth />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} mb={2}>Bank Details</Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField label="Bank Name" value={bank.bankName} onChange={e => setBank({ ...bank, bankName: e.target.value })} fullWidth />
                <TextField label="Account Number" value={bank.accountNumber} onChange={e => setBank({ ...bank, accountNumber: e.target.value })} fullWidth />
                <TextField label="IFSC Code" value={bank.ifscCode} onChange={e => setBank({ ...bank, ifscCode: e.target.value })} fullWidth />
                <TextField label="Branch" value={bank.branch} onChange={e => setBank({ ...bank, branch: e.target.value })} fullWidth />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} mb={2}>UPI Details</Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField label="UPI ID" value={upi.upiId} onChange={e => setUpi({ upiId: e.target.value })} fullWidth />
                <Box
                  sx={{
                    border: '2px dashed',
                    borderColor: 'divider',
                    borderRadius: 2,
                    p: 4,
                    textAlign: 'center',
                    color: 'text.secondary',
                  }}
                >
                  <Typography variant="body2">QR Code Placeholder</Typography>
                  <Typography variant="caption">Upload QR code image here</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
        <Button variant="contained" startIcon={<MdSave />} onClick={handleSave} size="large">
          Save Settings
        </Button>
      </Box>
    </Box>
  );
}
