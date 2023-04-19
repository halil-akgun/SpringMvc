package com.tpe.controller;

import com.tpe.domain.Student;
import com.tpe.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/students") // http://localhost:8080/SpringMvc/students
// class level: class icindeki tum methodlar icin
// method level: sadece o method icin mapping yapar
public class StudentController {

    @Autowired
    private StudentService service;

    // controllerdan requeste gore geriye ModelAndView(data+view name) veya
    // String tipinde view name dondurulur
    //@RequestMapping("/students/hi") // method level (tek method icin)
    @GetMapping("/hi")
    public ModelAndView ModelAndViewsayHi() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "HI, ");
        mav.addObject("messagebody", "I'm a Student Management System");
        mav.setViewName("hi"); // hi.jsp
        return mav;
    }

    // view resolver mov icindeki view name'e gore hi.jsp dosyasini bulur.
    // mov icindeki model'i hi.jsp icerisine bind eder (yerlestirir)

    // 1- Student Creation
    // kullanicidan bilgileri almak icin form gosterelim
    @GetMapping("/new") // http://localhost:8080/SpringMvc/students/new
    public String sendStudentForm(@ModelAttribute("student") Student student) {
        return "studentForm";
    } // @ModelAttribute : formdaki bilgilerle std objesi olusturur, sonra bu objenin kullanilmasini saglar
    // submit: http://localhost:8080/SpringMvc/students/saveStudent method: POST

    @PostMapping("/saveStudent")
    public String createStudent(@ModelAttribute("student") Student student) { // tum listeyi dondurebilmek icin return String
        // @ModelAttribute() olarak da yazilabilir
        service.saveStudent(student);
        return "redirect:/students"; // asagidaki linke yonlendirir
    }

    // tum stdlari listeleyen request: http://localhost:8080/SpringMvc/students
    @GetMapping
    public ModelAndView listAllStudents() {
        List<Student> students = service.getAll();
        ModelAndView mov = new ModelAndView();
        mov.addObject("studentList", students);//students.jsp'deki isimle ayni olmali(altta)
        // 			<c:forEach items="${studentList}" var="student" varStatus="status">
        mov.setViewName("students");//students.jsp
        return mov;
    }


    @GetMapping("/update")
    public ModelAndView showFormForUpdate(@RequestParam("id") Long id) { // dolu form donecegi icin return ModelAndView
        Student foundStudent = service.getStudentById(id);
        ModelAndView mov = new ModelAndView();
        mov.addObject("student", foundStudent);
        // studentForm'da student model'ina foundStudent'i bind et
        mov.setViewName("studentForm");
        return mov;
    }
}
