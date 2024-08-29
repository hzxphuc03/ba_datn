package datn.be.mycode.RESTController;

import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.TheLoaiRequest;
import datn.be.mycode.request.TrangThaiRequest;
import datn.be.mycode.response.NormalTableResponse;
import datn.be.mycode.service.TheLoaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/the_loai")
public class TheLoaiController {

    @Autowired
    private TheLoaiService theLoaiService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status
    ){
        if(page == null){
            page = 0;
        }
        if(pageSize == null){
            pageSize = 10;
            System.out.println("ahihi");
        }
        if(keyWord == null){
            keyWord = "";
            System.out.println("ahihi");
        }
        if(status == null){
            status = 1;
            System.out.println("ahihi");
        }


        NormalTableRequest request = new NormalTableRequest(page,pageSize,keyWord,status);
        return ResponseEntity.ok(theLoaiService.getAll(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody TheLoaiRequest request){

        return ResponseEntity.ok(theLoaiService.add(request));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody TheLoaiRequest request){

        return ResponseEntity.ok(theLoaiService.update(request));
    }

//    @PutMapping("/delete/{id}")
//    public ResponseEntity<?> delete(@PathVariable("id") Long id){
//
//        return ResponseEntity.ok(theLoaiService.deleteById(id));
//    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(theLoaiService.udateTrangThai(id, status));
    }
}
