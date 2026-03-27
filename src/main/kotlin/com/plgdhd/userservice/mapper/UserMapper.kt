package com.plgdhd.userservice.mapper

import com.plgdhd.userservice.dto.request.UpdateUserRequest
import com.plgdhd.userservice.dto.response.UserPrivateResponse
import com.plgdhd.userservice.dto.response.UserPublicResponse
import com.plgdhd.userservice.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
interface UserMapper {


    fun toPublicResponse(user: User): UserPublicResponse

    fun toPrivateResponse(user: User): UserPrivateResponse

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    fun toEntity(request: UpdateUserRequest): User


}