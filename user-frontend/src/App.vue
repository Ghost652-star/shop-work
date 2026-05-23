<template>
  <router-view />
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
@import './styles/variables.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
  scroll-behavior: smooth;
}

body {
  font-family: var(--font-body);
  color: var(--color-text-primary);
  background: var(--color-bg);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

img {
  display: block;
  max-width: 100%;
}

a {
  text-decoration: none;
  color: inherit;
}

/* 全局滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #D0D0D0;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #B0B0B0;
}

/* Element Plus 微调 */
.el-message {
  font-family: inherit !important;
}
</style>
