package com.userfront.service.UserServiceImpl;

import java.util.List;

import com.userfront.Dao.AppointmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.domain.Appointment;
import com.userfront.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDao appointmentDao;

    public Appointment createAppointment(Appointment appointment) {
       return appointmentDao.save(appointment);
    }

    public List<Appointment> findAll() {
        return appointmentDao.findAll();
    }

    public Appointment findAppointment(Long id) {
//        return appointmentDao.findOne(id);

        //CrudRepository has been updated with new methods and deprecated older methods.
        //The new replacement method for findOne(id) is now findById(id).
        //However, it's return type is optional<> . To get by this, you would need to have this in your code to replace findOne(id).
        return appointmentDao.findById(id).orElse(null);
    }

    public void confirmAppointment(Long id) {
        Appointment appointment = findAppointment(id);
        appointment.setConfirmed(true);
        appointmentDao.save(appointment);
    }
}
