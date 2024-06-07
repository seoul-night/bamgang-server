package growthook.org.bamgang.trail.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "trail")
@Getter
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_id")
    private Integer id;

    @Column(name="trail_title")
    private String title;

    @Column(name = "trail_detail")
    private String detail;

    @Column(name = "trail_image")
    private String image;

    @Column(name = "trail_distance")
    private Double distance;

    @Column(name = "trail_rating")
    private Double rating;

    @Column(name = "trail_time")
    private Double time;

    @Column(name = "trail_level")
    private String level;

    @Column(name = "trail_region")
    private String region;

    @Column(name = "latitude_list")
    private Double[] latitudeList;

    @Column(name = "longitude_list")
    private Double[] longitudeList;

    // TrailStart와의 일대일 관계 설정
    @OneToOne
    @JoinColumn(name = "trail_id", referencedColumnName = "trail_id", insertable = false, updatable = false)
    private TrailStart trailStart;
}

