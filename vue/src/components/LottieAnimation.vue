
<!-- LottieAnimation.vue -->
<template>
  <div ref="lottieContainer" class="lottie-container"></div>
</template>

<script>
import Lottie from 'lottie-web';

export default {
  name: "LottieAnimation",
  props: {
    animationData: {
      type: Object,
      required: true,
    },
    loop: {
      type: Boolean,
      default: true,
    },
    autoplay: {
      type: Boolean,
      default: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      Lottie.loadAnimation({
        container: this.$refs.lottieContainer,
        renderer: 'svg',
        loop: this.loop,
        autoplay: this.autoplay,
        animationData: this.animationData,
      });
    });
  },
};
</script>

<style>
.lottie-container {
  width: 100%;
  height: 100%;
}
</style>