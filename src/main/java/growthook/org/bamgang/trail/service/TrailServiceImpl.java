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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrailServiceImpl implements TrailService {

    public final TrailRepository trailRepository;
    public final TrailStartRepository trailStartRepository;
    public final DataPickedWalkRepository dataPickedWalkRepository;

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
        PickedWalk pickedWalk = dataPickedWalkRepository.findByUserIdAndTrailId(userId, trailId);
        boolean pick = pickedWalk != null;
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

    @Override
    public List<GetTrailResponseDto> getNearTrail(Double latitude, Double longitude) {
        Double minLatitude = latitude - 0.05;
        Double maxLatitude = latitude + 0.05;
        Double minLongitude = longitude - 0.05;
        Double maxLongitude = longitude + 0.05;

        List<Integer> nearTrailStarts = trailStartRepository.findTrailIdsByCoordinates(
                minLatitude, maxLatitude, minLongitude, maxLongitude,
                minLatitude, maxLatitude, minLongitude, maxLongitude,
                minLatitude, maxLatitude, minLongitude, maxLongitude
        );

        List<Trail> nearByTrails = nearTrailStarts.stream()
                .map(trailRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        nearByTrails.sort(Comparator.comparingDouble(trail -> calculateDistance(latitude, longitude, trail.getTrailStart().getStartLatitude1(), trail.getTrailStart().getStartLongitude1())));

        return nearByTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private double calculateDistance(Double userLat, Double userLon, Double trailLat, Double trailLon) {
        final int R = 6371; // 지구의 반경 (단위: km)
        double latDistance = Math.toRadians(trailLat - userLat);
        double lonDistance = Math.toRadians(trailLon - userLon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(trailLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 반환 (단위: km)
    }

    @Override
    public List<GetTrailResponseDto> getNearAttractionTrail(Double latitude, Double longitude) {
        Double minLatitude = latitude - 0.2;
        Double maxLatitude = latitude + 0.2;
        Double minLongitude = longitude - 0.3;
        Double maxLongitude = longitude + 0.3;

        List<Integer> nearTrailStarts = trailStartRepository.findTrailIdsByCoordinates(
                minLatitude, maxLatitude, minLongitude, maxLongitude,
                minLatitude, maxLatitude, minLongitude, maxLongitude,
                minLatitude, maxLatitude, minLongitude, maxLongitude
        );

        List<Trail> nearByTrails = nearTrailStarts.stream()
                .map(trailRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return nearByTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTrailResponseDto> getPopularTrail() {
        List<Trail> popularTrails = trailRepository.findTop10ByOrderByRatingDesc();

        return popularTrails.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetTrailResponseDto convertToResponseDto(Trail trail) {
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
