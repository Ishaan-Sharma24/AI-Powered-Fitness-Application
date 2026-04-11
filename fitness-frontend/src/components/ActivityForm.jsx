import {
    Box,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField,
    Typography,
    Paper
  } from '@mui/material';
  import React, { useState } from 'react';
  
  const ActivityForm = ({onActivityAdded}) => {
    const [activity, setActivity] = useState({
      type: "RUNNING",
      duration: '',
      caloriesBurned: '',
      additionalMetrics: {}
    });
    const handleSubmit = async(e)=>{
        e.preventDefault();
        try {
            //await addActivity(activity);
            onActivityAdded();
            setActivity({
                type: "RUNNING",
                duration: '',
                caloriesBurned: '',
                additionalMetrics: {}
              });
            
        } catch (error) {
            console.error(error);
        }
    }
  
  
  
    return (
      <Paper
        elevation={4}
        sx={{
          maxWidth: 400,
          margin: 'auto',
          mt: 5,
          p: 3,
          borderRadius: 3
        }}
      >
        <Typography variant="h5" sx={{ mb: 2, fontWeight: 'bold' }}>
          Add Activity
        </Typography>
  
        <Box component="form" onSubmit={handleSubmit}>
          
          {/* Activity Type */}
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel>Activity Type</InputLabel>
            <Select
              value={activity.type}
              label="Activity Type"
              onChange={(e) =>
                setActivity({ ...activity, type: e.target.value })
              }
            >
              <MenuItem value="RUNNING">Running</MenuItem>
              <MenuItem value="WALKING">Walking</MenuItem>
              <MenuItem value="CYCLING">Cycling</MenuItem>
            </Select>
          </FormControl>
  
          {/* Duration */}
          <TextField
            fullWidth
            label="Duration (minutes)"
            type="number"
            sx={{ mb: 2 }}
            value={activity.duration}
            onChange={(e) =>
              setActivity({ ...activity, duration: e.target.value })
            }
          />
  
          {/* Calories */}
          <TextField
            fullWidth
            label="Calories Burned"
            type="number"
            sx={{ mb: 3 }}
            value={activity.caloriesBurned}
            onChange={(e) =>
              setActivity({ ...activity, caloriesBurned: e.target.value })
            }
          />
  
          {/* Button */}
          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{
              py: 1.2,
              fontWeight: 'bold',
              borderRadius: 2
            }}
          >
            Add Activity
          </Button>
  
        </Box>
      </Paper>
    );
  };
  
  export default ActivityForm;