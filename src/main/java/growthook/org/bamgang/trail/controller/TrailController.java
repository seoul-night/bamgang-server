package growthook.org.bamgang.trail.controller;

import growthook.org.bamgang.trail.domain.Trail;
import growthook.org.bamgang.trail.dto.response.GetTrailResponseDto;
import growthook.org.bamgang.trail.service.TrailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/walks")
public class TrailController {
    private final TrailService trailService;
    public TrailController(TrailService trailService) {
        this.trailService = trailService;
    }

    // 산책로 상세정보 조회
    @GetMapping("/{trailId}")
    public GetTrailResponseDto getTrailById(@PathVariable("trailId") Integer trailId){
        return trailService.getTrailById(trailId);
    }

    // 사용자 위치(위도, 경도) 주변 산책로 조회
    @GetMapping("/near/{latitude}/{longitude}")
    public List<GetTrailResponseDto> getNearTrail(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude){
        return trailService.getNearTrail(latitude, longitude);
    }

    // 인기 산책로 조회
    @GetMapping("/popular")
    public List<GetTrailResponseDto> getPopularTrail(){
        return trailService.getPopularTrail();
    }

}
