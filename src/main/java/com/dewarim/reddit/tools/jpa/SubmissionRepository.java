package com.dewarim.reddit.tools.jpa;

import com.dewarim.reddit.tools.model.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission,String> {




}
