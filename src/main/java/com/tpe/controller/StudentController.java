package com.tpe.controller;

import com.tpe.domain.Student;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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

//    @PostMapping("/saveStudent") // @Valid: @NotBlank...lari kontrol et
//    public String createStudent(@Valid @ModelAttribute("student") Student student) { // tum listeyi dondurebilmek icin return String
//        // @ModelAttribute() olarak da yazilabilir - ("student") zorunlu degil
//        service.saveStudent(student);
//        return "redirect:/students"; // asagidaki linke yonlendirir
//    }

    // @NotBlank... kontrollerinde "HTTP Status 400 – Bad Request" yerine mevcut sayfada hata mesaji icin:
    @PostMapping("/saveStudent") // @Valid: @NotBlank...lari kontrol et
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult bindingResult) { // tum listeyi dondurebilmek icin return String
        //BindingResult: hatayi buradan okur:(stdForm.jsp)<td><form:errors path="firstName" class="error" /></td>
        if (bindingResult.hasErrors()) return "studentForm";
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


    // update: http://localhost:8080/SpringMvc/students/update?id=3
//    @GetMapping("/update")                  // RequestParam("id") = ?id=  // birden fazla degisken oldugunda tercih edlr
//    public ModelAndView showFormForUpdate(@RequestParam("id") Long id) { // dolu form donecegi icin return ModelAndView
////                                                                         GetMapping("/new")'de return String idi
//        Student foundStudent = service.getStudentById(id);
//        ModelAndView mov = new ModelAndView();
//        mov.addObject("student", foundStudent);// studentForm'da student model'ina foundStudent'i bind et
//        mov.setViewName("studentForm");
//        return mov;
//    }

    @GetMapping("/update")
    public String showFormForUpdate(@RequestParam("id") Long id, Model model) {
        Student foundStudent = service.getStudentById(id);
        model.addAttribute("student", foundStudent);
        return "studentForm";
    }

    // delete: http://localhost:8080/SpringMvc/students/delete/3
    @GetMapping("/delete/{id}") // burada $ yok
    public String deleteStudent(@PathVariable("id") Long id) {//requestle gelen id'yi PathVariable ile al Long id'ye koy
        service.deleteStudent(id);
        return "redirect:/students";
    }

    // exception handle
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView notFoundException(Exception ex) {
        ModelAndView mov = new ModelAndView();
        mov.addObject("message", ex.getMessage());
        mov.setViewName("notFound");
        return mov;
    } // ExceptionHandler belirtilen exception sinifi icin bir method belirlememizi saglar
    // bu method exceptioni yakalar ve ozel bir islem(notFound.jsp gosterilmesi) uygular


    // restful service: tum kayitlari dondur: http://localhost:8080/SpringMvc/students/restAll
    @GetMapping("/restAll")
    @ResponseBody // response'un dogrudan HTTP'ye json olarak yazilmasini saglar
    public List<Student> getAllStudents() {
        return service.getAll();
    }
}
/*
@GetMapping
    @RequestMapping("/update") kullanimi
ile
    @GetMapping("/update") kullanimi
arasinda fark var mi

chatGPT:
@GetMapping("/update") doğrudan /update istek yoluyla tetiklenecek bir GET isteğine karşılık gelirken,
@RequestMapping("/update") tüm HTTP metodlarını (GET, POST, PUT, DELETE vb.) /update istek yolunu eşleştirir.

Bu nedenle, @RequestMapping("/update") kullanımı, /update istek yolunu kullanarak farklı HTTP metodlarını yönetmek
istediğiniz durumlarda daha esnek bir yaklaşım sunar. Ancak sadece GET istekleriyle sınırlıysanız,
@GetMapping("/update") kullanımı daha açıklayıcı ve okunabilir olabilir.

Bununla birlikte, @RequestMapping ayrıca daha geniş kapsamlı ve karmaşık URL eşleştirmeleri ve özel parametreler
belirtmek için kullanılabilir, bu da @GetMapping gibi tek bir HTTP metoduyla sınırlı olan belirli durumlarda yararlı olabilir.
 */