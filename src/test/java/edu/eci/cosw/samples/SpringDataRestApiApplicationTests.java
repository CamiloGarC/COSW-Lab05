package edu.eci.cosw.samples;


import edu.eci.cosw.example.persistence.PatientsRepository;
import edu.eci.cosw.jpa.sample.model.Consulta;
import edu.eci.cosw.jpa.sample.model.Paciente;
import edu.eci.cosw.jpa.sample.model.PacienteId;
import edu.eci.cosw.samples.services.PatientServices;
import edu.eci.cosw.samples.services.ServicesException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringDataRestApiApplication.class)
@WebAppConfiguration

@ActiveProfiles("test")
public class SpringDataRestApiApplicationTests {

        
@Autowired
    private PatientsRepository RepositoryPatient;

    @Autowired
    private PatientServices PatientServer;

    @Test
	public void existeUnPaciente(){

        PacienteId pId = new PacienteId(1016068998, "cc");
        Paciente paciente = new Paciente(pId, "Camilo Garcia", new Date());
        RepositoryPatient.save(paciente);
        
        try {
            Paciente pacienteConsulta = PatientServer.getPatient(1016068998, "cc");
            Assert.assertEquals(pacienteConsulta.getId().getId()+pacienteConsulta.getId().getTipoId(),paciente.getId().getId()+paciente.getId().getTipoId());
        } catch (ServicesException ex) {
            Logger.getLogger(SpringDataRestApiApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("Patient Exist");
        }
    }

    @Test
    public void noExistePaciente(){

        RepositoryPatient.deleteAll();
        PacienteId pid=new PacienteId(1016068998,"cc");
        Paciente paciente = new Paciente(pid, "Camilo Garcia", new Date(1994,01,01));
        Paciente pacientePru=RepositoryPatient.findOne(pid);
        Assert.assertNull("No existe",pacientePru);
    }

    @Test
    public void noExistePacientesConNConsultas() throws ServicesException {

        RepositoryPatient.deleteAll();
        PacienteId pacienteId=new PacienteId(1016068998,"cc");
        Paciente paciente = new Paciente(pacienteId, "Camilo Garcia", new Date(2001,10,12));
        paciente.getConsultas().add(new Consulta(new Date(2012,5,21), "floo"));
        RepositoryPatient.save(paciente);
        List<Paciente> pacientes = PatientServer.topPatients(2);
        Assert.assertEquals("Empty List",pacientes.size(),0);

    }

    @Test
    public void existePacientesConNConsultas() throws ServicesException {

        RepositoryPatient.deleteAll();
        PacienteId pacienteid=new PacienteId(1016068998,"cc");
        Paciente paciente = new Paciente(pacienteid, "Camilo Garcia", new Date(1994,02,01));  
        
        
        PacienteId pacienteid2=new PacienteId(940909022,"cc");
        Paciente paciente2 = new Paciente(pacienteid2, "Felipe Garcia", new Date(2005,02,14)); 
        
        
        PacienteId pacienteid3=new PacienteId(9876541,"cc");
        Paciente paciente3 = new Paciente(pacienteid3, "Liliana Cabrera", new Date(1900,10,11));

        RepositoryPatient.save(paciente);
        
        
        paciente2.getConsultas().add(new Consulta(new Date(1994,02,01), "Pain"));
        RepositoryPatient.save(paciente2);

        paciente3.getConsultas().add(new Consulta(new Date(2005,02,14), "Haeadheach"));
        paciente3.getConsultas().add(new Consulta(new Date(1900,10,11), "Floo"));
        RepositoryPatient.save(paciente3);

        List<Paciente> pacientes = PatientServer.topPatients(1);

        Assert.assertEquals("list",pacientes.size(),2);
    }

}
