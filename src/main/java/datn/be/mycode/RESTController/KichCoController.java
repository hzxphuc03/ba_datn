package datn.be.mycode.RESTController;

import datn.be.mycode.entity.KichCo;
import datn.be.mycode.request.KichCoRequest;
import datn.be.mycode.request.NormalTableRequest;
import datn.be.mycode.request.TheLoaiRequest;
import datn.be.mycode.service.GiamGiaService;
import datn.be.mycode.service.KichCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/kich_co")
public class KichCoController {
    @Autowired
    private KichCoService kichCoService;
    @GetMapping("")
    public ResponseEntity<?> getall(
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
        return ResponseEntity.ok(kichCoService.getALL(request));
    }
    @PostMapping("/add")
    public ResponseEntity add(@RequestBody KichCoRequest request){
        return ResponseEntity.ok(kichCoService.add(request));
    }
    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody KichCoRequest request){

        return ResponseEntity.ok(kichCoService.update(request));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        return ResponseEntity.ok(kichCoService.deleteById(id));
    }

}
