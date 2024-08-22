package datn.be.mycode.RESTController;

import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.service.TheLoaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/the_loai")
public class TheLoaiController {

    @Autowired
    private TheLoaiService theLoaiService;

    @GetMapping("")
    public List<TheLoai> getAll(){
        return theLoaiService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<TheLoai> getById(@PathVariable("id") Long id){
        return theLoaiService.getById(id);
    }

    @PostMapping("/add")
    public List<TheLoai> add(@RequestBody TheLoai theLoai){
        theLoaiService.add(theLoai);
        return theLoaiService.getAll();
    }

    @PutMapping("update")
    public List<TheLoai> update(@RequestBody TheLoai theLoai){
        theLoaiService.update(theLoai);
        return theLoaiService.getAll();
    }

    @DeleteMapping("/delete")
    public List<TheLoai> delete(@RequestBody TheLoai theLoai){
        theLoaiService.delete(theLoai);
        return theLoaiService.getAll();
    }

    @DeleteMapping("/delete/{id}")
    public List<TheLoai> delete(@PathVariable("id") Long id){
        theLoaiService.deleteById(id);
        return theLoaiService.getAll();
    }
}
