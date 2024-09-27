package datn.be.mycode.RESTController;

import datn.be.mycode.request.NhanVienRequest;
import datn.be.mycode.request.NormalTableRequest;

import datn.be.mycode.service.NhanVienService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/nhan_vien")
public class NhanVienCotroller {

    @Autowired
    private NhanVienService NhanVienService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "pageSize",required = false) Integer pageSize,
            @RequestParam(name = "keyWord",required = false) String keyWord,
            @RequestParam(name = "status",required = false) Integer status
    ){

        NormalTableRequest request = new NormalTableRequest(page,pageSize,keyWord,status);
        return ResponseEntity.ok(NhanVienService.getAll(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid NhanVienRequest request){
        System.out.println(request);
        return ResponseEntity.ok(NhanVienService.add(request));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody NhanVienRequest request){

        return ResponseEntity.ok(NhanVienService.update(request));
    }

   @PutMapping("/delete/{id}")
  public ResponseEntity<?> delete(@PathVariable("id") Long id){

      return ResponseEntity.ok(NhanVienService.deleteById(id));
   }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<?> updateTrangThai(@PathVariable("id") Long id,
                                             @RequestParam(name = "status",required = true) Integer status
    ){

        return ResponseEntity.ok(NhanVienService.udateTrangThai(id, status));
    }
}
