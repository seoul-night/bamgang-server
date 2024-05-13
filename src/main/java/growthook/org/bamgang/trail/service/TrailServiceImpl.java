package growthook.org.bamgang.trail.service;

import growthook.org.bamgang.members.domain.PickedWalk;
import growthook.org.bamgang.members.repository.DataPickedWalkRepository;
import growthook.org.bamgang.trail.domain.Trail;
import growthook.org.bamgang.trail.domain.TrailStart;
import growthook.org.bamgang.trail.dto.response.GetTrailResponseDto;
import growthook.org.bamgang.trail.repository.TrailRepository;
import growthook.org.bamgang.trail.repository.TrailStartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrailServiceImpl implements TrailService{

    public final TrailRepository trailRepository;
    public final TrailStartRepository trailStartRepository;

    public final DataPickedWalkRepository dataPickedWalkRepository;

    // 산책로 상세정보 조회
    @Autowired
    public TrailServiceImpl(TrailRepository trailRepository, TrailStartRepository trailStartRepository, DataPickedWalkRepository dataPickedWalkRepository) {
        this.trailRepository = trailRepository;
        this.trailStartRepository = trailStartRepository;
        this.dataPickedWalkRepository = dataPickedWalkRepository;
    }
    @Override
    public GetTrailResponseDto getTrailById(Integer trailId, Integer userId) {
        Trail trail = trailRepository.findById(trailId)
                .orElseThrow(() -> new RuntimeException("Trail not found with id: " + trailId));
        PickedWalk pickedWalk = dataPickedWalkRepository.findByUserIdAndTrailId(userId,trailId);
        boolean pick = true;
        if(pickedWalk==null) pick = false;
        // Domain 객체를 Response DTO로 변환
        return GetTrailResponseDto.builder()
                .id(trail.getId())
                .title(trail.getTitle())
                .detail(trail.getDetail())
                .image(trail.getImage())
                .distance(trail.getDistance())
                .rating(trail.getRating())
                .time(trail.getTime())
                .level(trail.getLevel())
                .region(trail.getRegion())
                .latitudeList(trail.getLatitudeList())
                .longitudeList(trail.getLongitudeList())
                .picked(pick)
                .build();
    }

    // 주변 산책로 조회
    @Override
    public List<GetTrailResponseDto> getNearTrail(Double latitude, Double longitude) {
        // 조회할 범위 설정
        Double minLatitude = latitude - 0.05;
        Double maxLatitude = latitude + 0.05;
        Double minLongitude = longitude - 0.05;
        Double maxLongitude = longitude + 0.05;

        // 해당 범위 내에 있는 주변 산책로들을 조회한다.
        List<Integer> nearTrailStarts = trailStartRepository.findTrailIdsByCoordinates(
                // 3개의 출발점을 모두 조회해야 하므로, 같은 파라미터를 3개씩 보내야 한다.
                minLatitude, maxLatitude,
                minLongitude, maxLongitude,
                minLatitude, maxLatitude,
                minLongitude, maxLongitude,
                minLatitude, maxLatitude,
                minLongitude, maxLongitude
        );

        // 조회된 TrailStart들의 trailId를 사용해여 해당하는 Trail 정보 조회
        List<Trail> nearByTrails = nearTrailStarts.stream()
                .map(trailRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // 가져온 리스트에서 산책로들을 GetTrailRepsponseDto로 변환하려 리스트로 반환함.
        return nearByTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    // 주변 산책로 조회
    @Override
    public List<GetTrailResponseDto> getNearAttractionTrail(Double latitude, Double longitude) {
        // 조회할 범위 설정
        Double minLatitude = latitude - 0.2;
        Double maxLatitude = latitude + 0.2;
        Double minLongitude = longitude - 0.3;
        Double maxLongitude = longitude + 0.3;

        // 해당 범위 내에 있는 주변 산책로들을 조회한다.
        List<Integer> nearTrailStarts = trailStartRepository.findTrailIdsByCoordinates(
                // 3개의 출발점을 모두 조회해야 하므로, 같은 파라미터를 3개씩 보내야 한다.
                minLatitude, maxLatitude,
                minLongitude, maxLongitude,
                minLatitude, maxLatitude,
                minLongitude, maxLongitude,
                minLatitude, maxLatitude,
                minLongitude, maxLongitude
        );

        // 조회된 TrailStart들의 trailId를 사용해여 해당하는 Trail 정보 조회
        List<Trail> nearByTrails = nearTrailStarts.stream()
                .map(trailRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // 가져온 리스트에서 산책로들을 GetTrailRepsponseDto로 변환하려 리스트로 반환함.
        return nearByTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    // 인기있는 산책로 리스트 가져오기.
    @Override
    public List<GetTrailResponseDto> getPopularTrail() {
        List<Trail> popularTrails = trailRepository.findTop10ByOrderByRatingDesc();

        // 가져온 trail list를 GetTrailResponseDto 리스트로 변환.
        return popularTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // trail객체를 GeteTrailResponseDto 객체로 바꿔주는 함수.
    @Override
    public GetTrailResponseDto convertToResponseDto(Trail trail) {

        // trail 정보를 GetTrailResponseDto로 변환
        return GetTrailResponseDto.builder()
                .id(trail.getId())
                .title(trail.getTitle())
                .detail(trail.getDetail())
                .image(trail.getImage())
                .distance(trail.getDistance())
                .rating(trail.getRating())
                .time(trail.getTime())
                .level(trail.getLevel())
                .region(trail.getRegion())
                .latitudeList(trail.getLatitudeList())
                .longitudeList(trail.getLongitudeList())
                .build();
    }


}
