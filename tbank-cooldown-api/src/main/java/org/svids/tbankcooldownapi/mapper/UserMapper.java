package org.svids.tbankcooldownapi.mapper;

import org.mapstruct.Mapper;
import org.svids.tbankcooldownapi.dto.profile.AutoCoolingDto;
import org.svids.tbankcooldownapi.dto.profile.ManualCoolingDto;
import org.svids.tbankcooldownapi.dto.profile.UserProfileDto;
import org.svids.tbankcooldownapi.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileDto toDto(User user, ManualCoolingDto manualCooling, AutoCoolingDto autoCooling);
}
