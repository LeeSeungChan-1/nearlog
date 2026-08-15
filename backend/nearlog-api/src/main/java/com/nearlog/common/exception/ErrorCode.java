package com.nearlog.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "COMMON_001",
            "잘못된 요청입니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON_999",
            "서버 내부 오류가 발생했습니다."
    ),
    // Auth
    INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_001",
            "유효하지 않은 Refresh Token입니다."
    ),
    EXPIRED_OR_REVOKED_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_002",
            "만료되었거나 폐기된 Refresh Token입니다."
    ),

    // User
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_001",
            "사용자를 찾을 수 없습니다."
    ),

    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "USER_002",
            "이미 사용 중인 이메일입니다."
    ),

    DUPLICATE_USERNAME(
            HttpStatus.CONFLICT,
            "USER_003",
            "이미 사용 중인 유저명입니다."
    ),

    // Follow
    SELF_FOLLOW_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "FOLLOW_001",
            "자기 자신을 팔로우할 수 없습니다."
    ),

    ALREADY_FOLLOWING(
            HttpStatus.CONFLICT,
            "FOLLOW_002",
            "이미 팔로우한 사용자입니다."
    ),

    FOLLOW_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FOLLOW_003",
            "팔로우 관계가 존재하지 않습니다."
    ),

    // Upload
    INVALID_FILE_TYPE(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_001",
            "지원하지 않는 이미지 형식입니다."
    ),

    FILE_TOO_LARGE(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_002",
            "파일 크기가 허용 범위를 초과했습니다."
    ),

    UPLOAD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "UPLOAD_003",
            "업로드 정보를 찾을 수 없습니다."
    ),

    UPLOAD_EXPIRED(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_004",
            "업로드 요청이 만료되었습니다."
    ),

    UPLOAD_NOT_COMPLETED(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_005",
            "업로드가 완료되지 않았습니다."
    ),

    INVALID_UPLOAD_PURPOSE(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_006",
            "업로드 용도가 올바르지 않습니다."
    ),

    UPLOAD_ALREADY_USED(
            HttpStatus.CONFLICT,
            "UPLOAD_007",
            "이미 사용된 업로드입니다."
    ),

    UPLOADED_OBJECT_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "UPLOAD_008",
            "업로드된 파일을 S3에서 확인할 수 없습니다."
    ),

    // Post
    POST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POST_001",
            "게시글을 찾을 수 없습니다."
    ),

    INVALID_POST_MEDIA_COUNT(
            HttpStatus.BAD_REQUEST,
            "POST_002",
            "게시글에는 1개 이상 10개 이하의 이미지를 등록해야 합니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}