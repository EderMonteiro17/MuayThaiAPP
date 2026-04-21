from enum import Enum
from typing import List

from pydantic import BaseModel, ConfigDict, Field


class FighterStance(str, Enum):
    orthodox = "orthodox"
    southpaw = "southpaw"


class ExperienceLevel(str, Enum):
    beginner = "beginner"
    medium = "medium"
    advanced = "advanced"


class PrimaryGoal(str, Enum):
    cardio = "cardio"
    technique = "technique"
    fightprep = "fightprep"


class UserProfilePayload(BaseModel):
    stance: FighterStance
    experience_level: ExperienceLevel
    primary_goal: PrimaryGoal
    level_library_filter: str
    burnout_threshold_seconds: int = Field(ge=10, le=60)
    exercise_exclusions: List[str] = Field(default_factory=list)
    limitation_notes: str = ""


class UserProfileDocument(UserProfilePayload):
    model_config = ConfigDict(populate_by_name=True)

    id: str | None = Field(default=None, alias="_id")
    user_id: str
