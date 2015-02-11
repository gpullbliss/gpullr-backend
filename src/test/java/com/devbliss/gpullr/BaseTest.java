package com.devbliss.gpullr;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.MockitoAnnotations;

/**
 * BaseTest for unit tests.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public abstract class BaseTest {

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

}
