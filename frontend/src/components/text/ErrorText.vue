<template>
  <span class="error-text">{{ text }}</span>
</template>

<script setup lang="ts">
defineProps({
  text: {
    type: String,
    required: true,
  },
});
</script>

<style scoped lang="scss">
@import "@/styles/mixins";
@import "@/styles/global";

.error-text {
  @include error-text-style($bg-color);
}
</style>
